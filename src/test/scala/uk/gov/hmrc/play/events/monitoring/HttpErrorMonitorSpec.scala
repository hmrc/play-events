/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, UpstreamErrorResponse}
import uk.gov.hmrc.play.events.handlers.EventHandler
import uk.gov.hmrc.play.events.AlertLevel._
import uk.gov.hmrc.play.events.Unknown

import scala.concurrent.ExecutionContext.Implicits.global

class HttpErrorMonitorSpec extends AnyWordSpecLike with MockitoSugar with Matchers {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  "HttpErrorMonitor" should {

    "generate Alert and Monitor events for HTTP 4XX error with NO alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val response: UpstreamErrorResponse = UpstreamErrorResponse("Error Msg", 403, 60)

      intercept[UpstreamErrorResponse] {
        Await.result(

          monitor() {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorEvent(source, response, Unknown))
      }
    }

    "generate Alert and Monitor events for HTTP 4XX error with alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val response: UpstreamErrorResponse = UpstreamErrorResponse("Error Msg", 403, 60)

      intercept[UpstreamErrorResponse] {
        Await.result(

          monitor("test-code") {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorEvent(source, response, "test-code"))
      }
    }

    "generate Alert and Monitor events for HTTP 5XX error with NO alert code" in new HttpErrorMonitor {
      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val response: UpstreamErrorResponse = UpstreamErrorResponse("Error Msg", 500, 60)

      intercept[UpstreamErrorResponse] {
        Await.result(

          monitor() {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorEvent(source, response, Unknown))
      }

    }

    "generate Alert and Monitor events for HTTP 5XX error with alert code" in new HttpErrorMonitor {
      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val response: UpstreamErrorResponse = UpstreamErrorResponse("Error Msg", 500, 60)

      intercept[UpstreamErrorResponse] {
        Await.result(

          monitor("test-code") {
            Future(throw response)
          },

          200 millis
        )

        verify(mockHandler).handle(DefaultHttpErrorEvent(source, response, "test-code"))
      }

    }

    "generate Alert and Monitor events for http-exceptions HttpException resulting from HTTP 4XX error with NO alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val exception4XX = new HttpException("Error Msg", 400)
      val event4XX: DefaultHttpErrorEvent = DefaultHttpErrorEvent(source, exception4XX, Unknown)

      event4XX.level should be (MAJOR)

      intercept[HttpException] {
        Await.result(

          monitor() {
            Future(throw exception4XX)
          },

          200 millis
        )

        verify(mockHandler).handle(event4XX)
      }
    }

    "generate Alert and Monitor events for http-exceptions HttpException resulting from HTTP 4XX error with alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val exception4XX = new HttpException("Error Msg", 400)
      val event4XX: DefaultHttpErrorEvent = DefaultHttpErrorEvent(source, exception4XX, "test-code")

      event4XX.level should be (MAJOR)

      intercept[HttpException] {
        Await.result(

          monitor("test-code") {
            Future(throw exception4XX)
          },

          200 millis
        )

        verify(mockHandler).handle(event4XX)
      }
    }

    "generate Alert and Monitor events for http-exceptions HttpException resulting from HTTP 5XX error with NO alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val exception5XX = new HttpException("Error Msg", 502)
      val event5XX: DefaultHttpErrorEvent = DefaultHttpErrorEvent(source, exception5XX, Unknown)

      event5XX.level should be (CRITICAL)

      intercept[HttpException] {
        Await.result(

          monitor() {
            Future(throw exception5XX)
          },

          200 millis
        )

        verify(mockHandler).handle(event5XX)
      }
    }

    "generate Alert and Monitor events for http-exceptions HttpException resulting from HTTP 5XX error with alert code" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val exception5XX = new HttpException("Error Msg", 502)
      val event5XX: DefaultHttpErrorEvent = DefaultHttpErrorEvent(source, exception5XX, "test-code")

      event5XX.level should be (CRITICAL)

      intercept[HttpException] {
        Await.result(

          monitor("test-code") {
            Future(throw exception5XX)
          },

          200 millis
        )

        verify(mockHandler).handle(event5XX)
      }
    }

    "not generate Alert and Monitor events for successful response" in new HttpErrorMonitor {

      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      Await.result(

        monitor() {
          Future("Hello")
        },

        200 millis
      )

      verifyNoInteractions(mockHandler)
    }

  }

}
