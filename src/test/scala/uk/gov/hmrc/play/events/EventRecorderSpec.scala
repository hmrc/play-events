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

package uk.gov.hmrc.play.events

import org.mockito.Mockito._
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.events.examples.ExampleAuditEvent
import uk.gov.hmrc.play.events.handlers.EventHandler

class EventRecorderSpec extends WordSpec with MockitoSugar {

  "EventRecorder" should {

    "send events to all handlers" in {
        implicit val hc = HeaderCarrier()

        val testHandlerOne = mock[EventHandler]
        val testHandlerTwo = mock[EventHandler]

        val testRecorder = new EventRecorder {
          override def eventHandlers: Set[EventHandler] = Set(testHandlerOne, testHandlerTwo)
        }

        val testEvent = ExampleAuditEvent(1, "EventRecorder")

        testRecorder.record(testEvent)
        testRecorder.record(testEvent)

        verify(testHandlerOne, times(2)).handle(testEvent)
        verify(testHandlerTwo, times(2)).handle(testEvent)
    }

  }

}
