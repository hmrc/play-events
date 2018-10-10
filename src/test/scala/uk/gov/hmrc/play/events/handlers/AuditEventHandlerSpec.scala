/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.play.events.handlers

import org.mockito.Mockito._
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.events.Loggable
import uk.gov.hmrc.play.events.examples.ExampleAuditEvent
import scala.concurrent.ExecutionContext.Implicits.global

class AuditEventHandlerSpec extends WordSpec with MockitoSugar {

  implicit val hc = HeaderCarrier()

  val mockAuditConnector = mock[AuditConnector]

  val handler = new AuditEventHandler {
    override val auditConnector: AuditConnector = mockAuditConnector
  }

  "AuditEventHandler" should {

    "handle Audit events" in {

      val auditEvent = ExampleAuditEvent(1, "handleAuditEvents")

      handler.handle(auditEvent)

      //Calling a nested future above, need to wait for it
      Thread.sleep(200)

      verify(mockAuditConnector).sendEvent(auditEvent.event)
    }

    "not handle Logger events" in {
      val loggerEvent = new Loggable {
        override def log: String = "test"
      }

      handler.handle(loggerEvent)

      //Calling a nested future above, need to wait for it
      Thread.sleep(200)

      verifyZeroInteractions(mockAuditConnector)
    }

  }

}
