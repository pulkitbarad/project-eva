#! /bin/bash
gsutil cp gs://deployment-nuocanvas-com/nuo-backend-server/jdk-8u181-linux-x64.tar.gz /home/nuocanvas/
tar xzvf /home/nuocanvas/jdk-8u181-linux-x64.tar.gz
gsutil cp gs://deployment-nuocanvas-com/nuo-backend-server/nuo-backend-nucleo.jar /home/nuocanvas/
nohup /home/nuocanvas/jdk1.8.0_181/bin/java -jar /home/nuocanvas/nuo-backend-nucleo.jar &
