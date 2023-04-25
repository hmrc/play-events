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
import uk.gov.hmrc.play.events.{Loggable, Recordable}

import scala.concurrent.ExecutionContext

object DefaultLoggerEventHandler extends LoggerEventHandler {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  def handleLoggable(loggable: Loggable): Unit = logger.info(s"event::logger::${loggable.log}")
}

trait LoggerEventHandler extends EventHandler {

  def handleLoggable(loggable: Loggable)

  override def handle(event: Recordable)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Unit = event match {
    case loggable: Loggable => handleLoggable(loggable)
    case _ =>
  }

}
