package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.demo.mapper")
public class DemoApplication {

	public static void main(String[] args) throws Exception {
//		executeCommand("docker stop rabbitmq");
//		executeCommand("docker stop redis");
//		executeCommand("docker stop elasticsearch");
//		executeCommand("docker stop mysql");
//		executeCommand("docker rm rabbitmq");
//		executeCommand("docker rm redis");
//		executeCommand("docker rm elasticsearch");
//		executeCommand("docker rm mysql");
//
//		executeCommand("docker build -f Dockerfile-rabbitmq -t test-rabbitmq-image .");
//		executeCommand("docker run -d --name rabbitmq -e RABBITMQ_DEFAULT_USER=myuser -e RABBITMQ_DEFAULT_PASS=mypassword -p 5672:5672 -p 15672:15672 -v C:/data/rabbit:/var/lib/rabbitmq/mnesia test-rabbitmq-image");
//		executeCommand("docker build -f Dockerfile-redis -t test-redis-image .");
//		executeCommand("docker run -d --name redis -p 6379:6379 -v C:/data/redis,destination=/data test-redis-image");
//		executeCommand("docker build -f Dockerfile-elasticsearch -t test-elasticsearch-image .");
//		executeCommand("docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e \"ELASTIC_PASSWORD=mypassword\" -e \"discovery.type=single-node\" -e \"ES_JAVA_OPTS=-Xms512m -Xmx512m\" -v C:/data/elasticsearch:/usr/share/elasticsearch/data test-elasticsearch-image");
//		executeCommand("docker build -f Dockerfile-mysql -t test-mysql-image .");
//		executeCommand("docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=mypassword -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypassword -e MYSQL_DATABASE=user -v C:/data/mysql:/var/lib/mysql -p 3306:3306 test-mysql-image");
//		Thread.sleep(5000);
		SpringApplication.run(DemoApplication.class, args);
	}

	public static void executeCommand(String command) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(command);

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}

		process.waitFor();
		reader.close();
	}
}
