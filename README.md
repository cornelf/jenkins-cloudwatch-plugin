# jenkins-cloudwatch-plugin
Jenkins publisher plugin for Amazon CloudWatch

## install

```
$ git clone https://github.com/y-matsuki/jenkins-cloudwatch-plugin.git
$ cd jenkins-cloudwatch-plugin
$ mvn clean install
```

You can upload `target/jenkins-cloudwatch-plugin.hpi` from your computer on the Advanced tab of the Manage Plugins page in Jenkins.

## memo

This plugin work by EC2 Instance Profile(IAM Role).
