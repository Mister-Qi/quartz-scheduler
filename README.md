"# quartz-scheduler" 

#### start the program in console
```shell
java -jar quartz-scheduler-0.0.1.jar
```
#### or start the program at background
```shell
nohup java -jar quartz-scheduler-0.0.1.jar &
```
#### the program pid will store at `/j2ee/scheduler/quartz_scheduler/runtime/quartz-scheduler-shutdown.pid`
use below command to accurately stop the program
```shell
kill $(cat /j2ee/scheduler/quartz_scheduler/runtime/quartz-scheduler-shutdown.pid)
```
