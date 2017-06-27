/*
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random

class RecordedSimulation extends Simulation {

	private def randomString = Random.alphanumeric.take(10).mkString
	
	private def user =  s"""{
	    "name": "$randomString",
	    "email": "$randomString@mail.de",
	    "password": "$randomString"
	  }"""

	val httpProtocol = http
		.baseURL("http://127.0.0.1:55")
		.inferHtmlResources()
		.acceptHeader("application/json,text/plain,text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*//**;q=0.8")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")

	val uril = "http://127.0.0.1:55/users"



	val scn = scenario("RecordedSimulation")
		.exec(http("get all Users")
			.get("/users"))
		.pause(2)
		.exec(http("get User by Id")
			.get("/users/id/592acc14204a0b1760e000dd"))
		.pause(2)
		.exec(http("get User by Email")
			.get("/users/email/"+randomString+"@mail.de"))
		.pause(2)
		.exec(http("Add new User") 
		      .post("/users")
		      .header("Content-Type", "application/json")
		      .body(StringBody(user.toString))
		      )
		

		setUp(scn.inject(rampUsers(4000) over 400)).protocols(httpProtocol)
}*/