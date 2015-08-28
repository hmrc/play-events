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

package uk.gov.hmrc.play.events.examples

import uk.gov.hmrc.play.events.DefaultEventRecorder
import uk.gov.hmrc.play.events.handlers.{DefaultAlertEventHandler, DefaultMetricsEventHandler, EventHandler}
import uk.gov.hmrc.play.events.{Measurable, AlertCode, FailureCode}
import uk.gov.hmrc.play.events.monitoring._

import scala.concurrent.duration.Duration

trait ExampleEventRecorder extends DefaultEventRecorder {

  override def eventHandlers: Set[EventHandler] = Set(DefaultMetricsEventHandler, DefaultAlertEventHandler)
}

trait ExampleHttpErrorMonitor extends HttpErrorMonitor with ExampleEventRecorder {

  override val source = "TestApp"
}

trait ExampleHttpErrorCountMonitor extends HttpErrorCountMonitor with ExampleEventRecorder {

  override val source = "TestApp"

  override def createHttpErrorCountEvent(alertCode: AlertCode, failureCode: FailureCode): Measurable = ExampleHttpErrorCountEvent(alertCode: String, failureCode: String)
}

case class ExampleHttpErrorCountEvent(alertCode: AlertCode, failureCode: FailureCode) extends Measurable {

  override val source = "TestApp"

  override def data: Map[String, String] = Map.empty

  override def name: String = s"HttpErrorCount-$alertCode-$failureCode"
}

trait ExampleTimer extends Timer with ExampleEventRecorder {

  override val source = "TestApp"

  override def createTimerEvent(alertCode: AlertCode, duration: Duration): Measurable = ExampleTimerEvent(alertCode, duration)
}

case class ExampleTimerEvent(alertCode: String, duration: Duration) extends Measurable {

  override val source = "TestApp"

  override def data: Map[String, String] = Map (
    "Time" -> s"${duration.length}"
  )

  override def name: String = s"Timer-$alertCode"
}