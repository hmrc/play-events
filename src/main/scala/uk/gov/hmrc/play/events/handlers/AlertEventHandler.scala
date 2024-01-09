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

package uk.gov.hmrc.play.events.handlers

import org.slf4j.{Logger, LoggerFactory}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.{Alertable, Recordable}

import scala.concurrent.ExecutionContext

object DefaultAlertEventHandler extends AlertEventHandler {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  override def handleAlertable(alertable: Alertable): Unit = logger.warn(s"alert:${alertable.level}:source:${alertable.source}:code:${alertable.alertCode}:name:${alertable.name}")
}

trait AlertEventHandler extends EventHandler {

  def handleAlertable(alertable: Alertable): Unit

  override def handle(recordable: Recordable)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Unit = recordable match {
    case alertable: Alertable => handleAlertable(alertable)
    case _ =>
  }

}
