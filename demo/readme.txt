Config options:
	- noPetrolPumps
	- pumpCapacity
	- cityNames
	- tickTime : interval to repeat noHits in o=iterations
	- allowedHitsPerTick : per tick interval, max. no of hits allowed per process

todo
    - update time everyy 1 sec
    - kafka produce

KAFKA
./zookeeper-server-start.sh ../config/zookeeper.properties
./zookeeper-shell.sh ../config/zookeeper.properties
/Users/raj/apps/zookeeper/bin/zkCli.sh
	ls /
	rmr /brokers

./kafka-server-start.sh ../config/server.properties
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic "test1" --from-beginning
./kafka-console-producer.sh --broker-list localhost:9092 --topic "test1"
./kafka-topics.sh --zookeeper localhost:2181 --delete --topic test1


PHP, JS
npm install --save d3
npm install --save c3
npm install --save jquery
npm install --save fusioncharts
npm install --save bootstrap

DB
use sample;
Create table tbl3(
	pdate DATE,
	city varchar(100),
	total_amt int
);

select pdate, sum(total_amt) from tbl3 group by pdate;
drop table tbl3;
truncate tbl3;

select * from tbl2;

Charts
http://localhost/php_charts/charts2.php
http://localhost/php_charts/charts1.php

