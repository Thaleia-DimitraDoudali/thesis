#!/bin/bash

#Put code jar in hbase lib folder!
export HBASE_CLASSPATH=$(/opt/hbase-0.94.27/bin/hbase classpath)
nohup java -cp java -cp $HBASE_CLASSPATH client.CheckInsQuery $1 $2 > chk-query.out &
