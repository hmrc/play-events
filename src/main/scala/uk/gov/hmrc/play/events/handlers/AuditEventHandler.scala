/*
 * Copyright 2020 HM Revenue & Customs
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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.events.{Auditable, Recordable}

import scala.concurrent.{ExecutionContext, Future}

abstract class AuditEventHandler extends EventHandler {

  def auditConnector: AuditConnector

  override def handle(recordable: Recordable)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Unit = {
    recordable match {
      case event: Auditable => handleAudit(event)
      case _ =>
    }
  }

  def handleAudit(auditable: Auditable)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Unit = {
    Future {
      auditConnector.sendEvent(auditable.event)
    }
  }

}
