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

import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.play.events.handlers.EventHandler

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class StackableMonitoringSpec extends AnyWordSpecLike with MockitoSugar with Matchers {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  "HttpErrorMonitor and HttpErrorCountMonitor" should {

    "generate Alert and Monitor events for HTTP error with alert code" in new HttpErrorMonitor with HttpErrorCountMonitor {
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
        verify(mockHandler).handle(DefaultHttpErrorCountEvent(source, response, "test-code"))
      }

    }
  }
}
