# myService
a webService


docker

docker build -f Dockerfile-rabbitmq -t test-rabbitmq-image .

docker run -d --name rabbitmq -e RABBITMQ_DEFAULT_USER=myuser -e RABBITMQ_DEFAULT_PASS=mypassword -v c:/data/rabbit:/var/lib/rabbitmq/mnesia -p 5672:5672 -p 15672:15672 test-rabbitmq-image

docker build -f Dockerfile-redis -t test-redis-image .

docker run -d --name redis -p 6379:6379 -v C:/data/redis,destination=/data test-redis-image

docker build -f Dockerfile-elasticsearch -t test-elasticsearch-image .

docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "ELASTIC_PASSWORD=mypassword" -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" -v c:/data/elasticsearch:/usr/share/elasticsearch/data test-elasticsearch-image

docker build -f Dockerfile-mysql -t test-mysql-image .

docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=mypassword -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypassword -e MYSQL_DATABASE=user -v c:/data/mysql:/var/lib/mysql -p 3306:3306 test-mysql-image .