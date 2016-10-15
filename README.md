# tide-backend
Visual Spark jobs contructor, using [Mist](https://github.com/Hydrospheredata/mist) as HTTP API to Apache Spark.

## How it works?
1. User draws data flow diagram on the front-end side.
2. Then diagram is sent to the back-end.
3. This JSON is then translated into Abstract Syntax Tree.
4. This AST is then translated into pySpark job source code.
5. Along with translation, request to Mist server is prepared.
6. After translation, job is copied to the Mist server.
7. Send request to Mist server to trigger the computation.
8. Send Mist response to the front-end, where results are parsed and proceeded further.

## Contribution
If you interested in this project, feel free to open Issues on the tracker, or submit your Pull Requests.

---
If there any questions, e.g. deploying this app, feel free to contact us.
