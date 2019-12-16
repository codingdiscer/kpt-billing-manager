	create user ndowma with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to ndowma;
	grant all privileges on all sequences in schema public to ndowma;
	grant select, insert, update, delete on all tables in schema public to ndowma;


	create user aherrman with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to aherrman;
	grant all privileges on all sequences in schema public to aherrman;
	grant select, insert, update, delete on all tables in schema public to aherrman;

	create user sgalloway with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to sgalloway;
	grant all privileges on all sequences in schema public to sgalloway;
	grant select, insert, update, delete on all tables in schema public to sgalloway;

	create user scurtis with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to scurtis;
	grant all privileges on all sequences in schema public to scurtis;
	grant select, insert, update, delete on all tables in schema public to scurtis;

	create user tneff with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to tneff;
	grant all privileges on all sequences in schema public to tneff;
	grant select, insert, update, delete on all tables in schema public to tneff;

	create user ddowma with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to ddowma;
	grant all privileges on all sequences in schema public to ddowma;
	grant select, insert, update, delete on all tables in schema public to ddowma;

	create user brisenhoover with encrypted password '{}';
	grant all privileges on database kpt_visit_tracker to brisenhoover;
	grant all privileges on all sequences in schema public to brisenhoover;
	grant select, insert, update, delete on all tables in schema public to brisenhoover;




	// local
	java -Dspring.datasource.url=jdbc:postgresql://192.168.99.100:5432/postgres  -jar kpt-billing-manager-all.jar

	// stage
	java -Dspring.datasource.url=jdbc:postgresql://{}/kpt_visit_tracker_stage  -jar kpt-billing-manager-all.jar

	// prod
	java -Dspring.datasource.url=jdbc:postgresql://{}/kpt_visit_tracker  -jar kpt-billing-manager-all.jar
