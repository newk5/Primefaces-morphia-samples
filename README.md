# Primefaces-morphia-samples
Contains a few samples of how to use morphia in combination with primefaces, for now only has an example of how to use a generic MorphiaLazyDataModel

You can run this with ```mvnw quarkus:dev``` or if you're using netbeans, right click the project--->goals--->RUN.

Keep in mind, this demo expects you to have a local mongodb instace running on port 27017 without authentication configured. If you want to use authentication, please configure the connection in  ```AppBean.java```
