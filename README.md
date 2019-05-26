Exchange Currency Service
==============================================

Java web service deployed by AWS Elastic Beanstalk and AWS CloudFormation. If you want to test service as a developer just click here http://exchange-servicapp.gre5dcxh5s.us-east-1.elasticbeanstalk.com/swagger-ui.html#/CurrencyService

What's Here
-----------
* README.md - this file
* .ebextensions/ - this directory contains configuration files that
  allows AWS Elastic Beanstalk to deploy your Java service
* buildspec.yml - this file is used by AWS CodeBuild to build the web
  service
* pom.xml - this file is the Maven Project Object Model for the web service
* src/main - this directory contains your Java service source files
* src/test - this directory contains your Java service unit test files
* template.yml - this file contains the description of AWS resources used by AWS
  CloudFormation to deploy your infrastructure
* template-configuration.json - this file contains the project ARN with placeholders used for tagging resources with the project ID

Done
---------------

 - The application should be deployed on a cloud-based service (e.g. Heroku, AWS or Azure)
 - Application should be unit tested
 - The web application will use the following API as its data source for historical values: https://www.alphavantage.co/documentation/#fx-daily
 - The web application will use the following API as its data source for real-time values: https://www.alphavantage.co/documentation/#currency-exchange
 - As a developer, I must be able to select currencies (available in the [](https://openexchangerates.org/api/currencies.json) to exchange)
 - As a developer, I am able to see the current exchange rate for selected currencies pair
 - As a developer, I am able to see the exchange rate historical chart for selected currencies pair


What Do I Do Next?
------------------

 - As a user, I must be able to select currencies (available in the https://openexchangerates.org/api/currencies.json) to exchange
 - As a user, I am able to see the current exchange rate for selected currencies pair
 - As a user, I am able to see the exchange rate historical chart for selected currencies pair
 - As a user, I am able to see growth trends are marked by the green line on the chart
 - As a user, I am able to see drop trends are marked by the red line on the chart
 - As a user, I am able to hide or show a trend lines
 - As a user, I am able to change the chart time range (x-axis). The available time range is limited by dates fetched from the data source.


