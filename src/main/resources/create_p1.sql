create database im;
use im;


create table User (id bigint unsigned not null auto_increment,
 firstname varchar(100)  not null,
 surname varchar(100) not null,
 password varchar(200) not null,
email varchar(200) not null,
 primary key(id)
 );
 
 
 
 create table Topic(
 id bigint unsigned not null auto_increment,
 name varchar(100)  not null unique,
topicmaster bigint unsigned,
CONSTRAINT  fk_user_1 foreign key(topicmaster) references User(id) on delete cascade,
 primary key(id));
 
 
  
 create table Topic_Faq(
 id bigint unsigned not null auto_increment,
 question varchar(1000)  not null ,
 answer varchar(1000)  not null ,
 topicid bigint unsigned,
CONSTRAINT  fk_topic_1 foreign key(topicid) references Topic(id) on delete cascade,
 primary key(id));
 

create table Logout_Token (
 id bigint unsigned not null auto_increment,
 token varchar(800) not null,
  primary key(id));
  
  alter table  Logout_Token add index token_logout_index(token);
  
  
  create table IM_Message(
   id bigint unsigned not null auto_increment,
   toUserId bigint unsigned not null,
   fromUserId bigint unsigned not null,
   topicId bigint unsigned not null,
   
   isgroupmessage bool not null,
   msgcontent varchar(5000) not null,
   time  bigint unsigned not null,
   primary key(id)
   );
   
   
     create table IM_Message_At_List(
   id bigint unsigned not null auto_increment,
   msgId bigint unsigned not null,
   atuserid bigint unsigned not null,
   CONSTRAINT  fk_message_1 foreign key(msgId) references IM_Message(id) on delete cascade,
	CONSTRAINT  fk_user_2  foreign key(atuserid) references User(id) on delete cascade,
   primary key(id)
   );
   
   
   create table Topic_Users (
	   id bigint unsigned not null auto_increment,
       
       bigint unsigned not null,
   userid bigint unsigned not null,
    time  bigint unsigned not null,
   CONSTRAINT  fk_topic_2 foreign key(topicid) references Topic(id) on delete cascade,
	CONSTRAINT  fk_user_3  foreign key(userid) references User(id) on delete cascade,
   primary key(id)
   );
   
   
   