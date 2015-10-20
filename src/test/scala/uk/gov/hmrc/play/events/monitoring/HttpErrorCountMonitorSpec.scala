/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.events.monitoring

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.{Upstream4xxResponse, Upstream5xxResponse, HttpException}
import uk.gov.hmrc.play.events.handlers.EventHandler
import uk.gov.hmrc.play.events.Unknown

class HttpErrorCountMonitorSpec extends WordSpec with MockitoSugar with Matchers {

  implicit val hc = new HeaderCarrier()

  "HttpErrorCountMonitor" should {

    "generate Monitor events for HTTP 4XX error with NO alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val response = new Upstream4xxResponse("Error Msg", 403, 60)

      intercept[Upstream4xxResponse] {
        Await.result(

          monitor() {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, response, Unknown))
      }
    }

    "generate Monitor events for HTTP 4XX error with alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val response = new Upstream4xxResponse("Error Msg", 403, 60)

      intercept[Upstream4xxResponse] {
        Await.result(

          monitor("test-code") {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, response, "test-code"))
      }
    }

    "generate Monitor events for HTTP 5XX error with NO alert code" in new HttpErrorCountMonitor {
      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val response = new Upstream5xxResponse("Error Msg", 500, 60)

      intercept[Upstream5xxResponse] {
        Await.result(

          monitor() {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, response, Unknown))
      }

    }

    "generate Monitor events for HTTP 5XX error with alert code" in new HttpErrorCountMonitor {
      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val response = new Upstream5xxResponse("Error Msg", 500, 60)

      intercept[Upstream5xxResponse] {
        Await.result(

          monitor("test-code") {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, response, "test-code"))
      }

    }

    "generate Monitor events for http-exceptions HttpException resulting from HTTP 4XX error with NO alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val exception4XX = new HttpException("Error Msg", 400)

      intercept[HttpException] {
        Await.result(

          monitor() {
            Future(throw exception4XX)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, exception4XX, Unknown))
      }
    }

    "generate Monitor events for http-exceptions HttpException resulting from HTTP 4XX error with alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val exception4XX = new HttpException("Error Msg", 400)

      intercept[HttpException] {
        Await.result(

          monitor("test-code") {
            Future(throw exception4XX)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, exception4XX, "test-code"))
      }
    }

    "generate Monitor events for http-exceptions HttpException resulting from HTTP 5XX error with NO alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val exception5XX = new HttpException("Error Msg", 502)

      intercept[HttpException] {
        Await.result(

          monitor() {
            Future(throw exception5XX)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, exception5XX, Unknown))
      }
    }

    "generate Monitor events for http-exceptions HttpException resulting from HTTP 5XX error with alert code" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val exception5XX = new HttpException("Error Msg", 502)

      intercept[HttpException] {
        Await.result(

          monitor("test-code") {
            Future(throw exception5XX)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, exception5XX, "test-code"))
      }
    }

    "not generate Monitor events for successful response" in new HttpErrorCountMonitor {

      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      Await.result(

        monitor() {
          Future("Hello")
        },

        200 millis
      )

      verifyZeroInteractions(mockHandler)
    }

  }

}