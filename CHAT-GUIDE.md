# Chat en tiempo real con RabbitMQ

## 1. Diseño integrado

El repositorio utiliza Angular standalone, un BFF y microservicios Spring Boot 3.3/Java 21. El chat se implementa como un servicio independiente `tienda-perritos-ms-chat`, usa la base PostgreSQL existente y valida el mismo JWT HS256 emitido por `ms-usuarios`. El `username` del `subject` del JWT funciona como `userId` de la conversación: el token actual no contiene el ID numérico de usuario.

El API de chat recibe mensajes autenticados, los publica como JSON en `chat.exchange` y RabbitMQ los copia a dos colas independientes. `ChatPersistenceConsumer` guarda una copia idempotente y `ChatDeliveryConsumer` publica a `/topic/chat.{userId}` por STOMP. El consumidor de entrega también publica avisos de nuevos mensajes de usuario en `/topic/chat.admin`, para mantener actualizada la lista del administrador.

```text
Angular --REST/JWT--> ms-chat --chat.user.{username} o chat.admin.{username}--> chat.exchange
                                                                        |--> chat.persist.queue --> PostgreSQL
                                                                        |--> chat.delivery.queue --> WebSocket/STOMP
Fallos tras 3 intentos -------------------------------------------------------> chat.dlq
```

El panel considera abiertas las conversaciones que ya tienen mensajes; no se incorporó un flujo de cierre porque no existe ese estado en los requisitos. Los mensajes se publican desde REST (nunca desde el navegador directamente a RabbitMQ ni por STOMP).

## 2. Archivos integrados

- Servicio nuevo: `tienda-perritos-ms-chat/` (POM, Dockerfile, configuración, seguridad JWT/STOMP, API, DTOs, entidad/repositorio, productor/consumidores y prueba de servicio).
- Infraestructura: `docker-compose.yml`, `rabbitmq.conf` y proxy de `/chat-api/` y `/chat-ws` en el `nginx.conf` del frontend.
- Angular: dependencia `@stomp/stompjs`, URLs de chat en ambos ambientes, interceptor JWT, widget USER, vista ADMIN, ruta protegida y enlace de navegación.

## 3. Dependencias y propiedades

Dependencias nuevas del servicio: `spring-boot-starter-amqp`, `spring-boot-starter-websocket`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, PostgreSQL JDBC y JJWT. El proyecto ya usa PostgreSQL, por lo que no se agrega una segunda BD H2.

`application.yml` configura `spring.rabbitmq.*`, ACK `auto`, reintentos hasta 3 veces, `prefetch: 1`, `concurrency: 1` y `max-concurrency: 3`. El converter Jackson de `RabbitMQConfig` serializa `ChatMessageDto`; no hay un segundo listener compitiendo por una misma cola.

## 4. Ejecución local (Windows PowerShell)

Desde `E1-Cloud-back`:

```powershell
docker compose up -d --build
docker compose ps
```

Compilación del servicio:

```powershell
Set-Location .\tienda-perritos-ms-chat
mvn clean package
Set-Location ..
docker compose stop ms-chat
Set-Location .\tienda-perritos-ms-chat
mvn spring-boot:run
```

Si se ejecuta el servicio desde Maven fuera de Docker, PostgreSQL y RabbitMQ deben estar activos y las variables `DATABASE_*`, `JWT_SECRET` y `RABBITMQ_*` deben apuntar a `localhost`. El frontend se levanta desde `E1-Cloud-front` con `npm install` y `npm start`; el lockfile debe quedar sincronizado con el POM de npm.

Frontend dentro de Compose: http://localhost. En desarrollo Angular: http://localhost:4200. El endpoint local del servicio de chat es `http://localhost:8083/api/v1`.

## 5. Prueba funcional

1. Inicia sesión como `user` en una ventana y como `admin` en otra (credenciales de desarrollo descritas en el README del backend).
2. En la ventana USER, abre el widget, envía un texto y luego prueba una solicitud de cotización con un `orderId`.
3. En la ventana ADMIN, abre el enlace **Chat**, selecciona la conversación y responde como texto o cotización (precio y comentario).
4. Comprueba que los mensajes aparecen en tiempo real en ambas ventanas y que la cotización tiene un formato distinto.
5. En http://localhost:15672 ingresa `guest` / `guest` y confirma que `chat.persist.queue` y `chat.delivery.queue` tienen consumidores activos; `chat.dlq` existe y normalmente está vacía.
6. Los logs del contenedor `ms-chat` muestran `[OK] Publicado`, `[OK] Persistido` y `[OK] Entregado`.
7. Comprueba persistencia revisando la tabla `chat_messages` en PostgreSQL y vuelve a cargar la conversación.

Comprobación rápida:

```powershell
docker compose logs -f ms-chat
docker compose exec postgres psql -U <DATABASE_USER> -d <DATABASE_NAME> -c "select message_id, conversation_id, sender_role, type, status, created_at from chat_messages order by created_at desc limit 10;"
```

## 6. Diagnóstico de errores comunes

- **`PRECONDITION_FAILED` al declarar una cola**: RabbitMQ ya tiene esa cola con durable/argumentos distintos. Detén el laboratorio y elimina solamente las colas de chat desde Management o recrea el volumen de RabbitMQ (esto borra sus datos); no elimines el volumen PostgreSQL.
- **Mensajes publicados pero no recibidos por el listener**: revisa que ambas colas estén enlazadas con `chat.#`, que el routing key sea `chat.user.{userId}`/`chat.admin.{userId}`, y que las colas tengan consumidores activos.
- **Mensajes repetidos o fallo al persistir**: el `messageId` UUID tiene índice único y los reintentos omiten un UUID ya guardado. Revisa `ms-chat` logs y, tras agotar 3 intentos, consulta `chat.dlq`.
- **`guest` rechazado desde `ms-chat`**: RabbitMQ limita `guest` a conexiones locales. `rabbitmq.conf` desactiva esa restricción solo para esta red de laboratorio; nunca reutilices este ajuste en producción.
- **WebSocket 404 / desconexión**: revisa la ruta completa `/api/v1/ws`, el proxy `/chat-ws`, y los encabezados `Upgrade` / `Connection` en Nginx.
- **`401` al conectar STOMP**: el navegador manda `Authorization: Bearer <JWT>` en el frame `CONNECT`; vuelve a iniciar sesión y confirma que `JWT_SECRET` coincide entre `ms-usuarios`, BFF y `ms-chat`.
- **`403` al suscribirse**: USER solo puede suscribirse a `/topic/chat.{su-username}`; el ADMIN puede abrir los topics de conversación y `/topic/chat.admin`.
- **No se ve el chat al correr `ng serve`**: usa `chatApiUrl`/`chatWsUrl` de `environment.ts`; en Compose, Angular usa rutas relativas proxificadas por Nginx.
- **Historial/lista vacía**: los listeners son asíncronos. Confirma conectividad con PostgreSQL/RabbitMQ y espera que aparezca `[OK] Persistido`.

`guest/guest` y `loopback_users.guest = false` son credenciales/configuración exclusivas para demostración local. En un despliegue real usa un usuario RabbitMQ dedicado, secreto externo y TLS.

## 7. Checklist de verificación

- [ ] `docker compose up -d --build` deja PostgreSQL, RabbitMQ, `ms-chat`, BFF y frontend saludables.
- [ ] `mvn clean package` termina correctamente en `tienda-perritos-ms-chat`.
- [ ] NPM instala la dependencia STOMP usando el lockfile del frontend.
- [ ] USER ve el widget únicamente al iniciar sesión como USER; ADMIN usa la vista protegida `/chat-admin`.
- [ ] USER no puede leer/escribir otra conversación, ni REST ni suscribiéndose a topics ajenos.
- [ ] ADMIN puede leer y contestar las conversaciones de la lista.
- [ ] Hay un listener por cada cola de consumo, conversión JSON, ACK automático, retry 3, y DLQ.
- [ ] UUID duplicado no crea otra fila; los mensajes quedan persistidos después de recargar.
- [ ] Solicitud y respuesta de cotización se representan distinto en ambas pantallas.
- [ ] La consola muestra logs `[OK]` y los errores muestran `[ERROR]`.

