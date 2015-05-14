# jenkins-sns-notification-plugin
Jenkins publisher plugin for AmazonSNS

## install

```
$ git clone https://github.com/y-matsuki/jenkins-sns-notification-plugin.git
$ cd jenkins-sns-notification-plugin
$ mvn clean install
```

You can upload `target/jenkins-sns-notification-plugin.hpi` from your computer on the Advanced tab of the Manage Plugins page in Jenkins.

## memo

This plugin work by EC2 Instance Profile(IAM Role).
If you want to use ACCESS_KEY and SECRET_KEY. you should use [Amazon SNS Notifier](https://wiki.jenkins-ci.org/display/JENKINS/Amazon+SNS+Notifier) plugin.
