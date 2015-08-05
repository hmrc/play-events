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

import uk.gov.hmrc.play.audit.model.{AuditEvent, DataEvent}
import uk.gov.hmrc.play.events.monitoring.HttpMonitor.AlertCode

trait Recordable

trait Auditable extends Recordable {

  def source: String
  def name: String
  def tags: Map[String, String]
  def privateData: Map[String, String]

  val event: AuditEvent =
    DataEvent(
      auditSource = source,
      auditType = name,
      tags = tags,
      detail = privateData)
}

trait Loggable extends Recordable {
  def log: String
}

trait Measurable extends Recordable {

  def source: String
  def name: String
  def data: Map[String, String]

}

object AlertLevel extends Enumeration {

  type AlertLevel = Value
  val WARNING, MINOR, MAJOR, CRITICAL = Value

}

import uk.gov.hmrc.play.events.AlertLevel._

trait Alertable extends Recordable {

  def source: String
  def name: String
  def level: AlertLevel
  def alertCode: AlertCode

}
