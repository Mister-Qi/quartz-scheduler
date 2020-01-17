#### The demo quartz-scheduler to schedule local script 

#### 1.start the program in console
```shell
java -jar quartz-scheduler-0.0.1.jar
```
#### 2.or start the program at background
```shell
nohup java -jar quartz-scheduler-0.0.1.jar &
```
#### 3.the program pid will store at `/j2ee/scheduler/quartz_scheduler/runtime/quartz-scheduler-shutdown.pid`
use below command to accurately stop the program
```shell
kill $(cat /j2ee/scheduler/quartz_scheduler/runtime/quartz-scheduler-shutdown.pid)
```
