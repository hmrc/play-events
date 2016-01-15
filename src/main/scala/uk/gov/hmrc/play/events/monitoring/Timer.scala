/*
 * Copyright 2016 HM Revenue & Customs
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

import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.events.{DefaultEventRecorder, Measurable, AlertCode, Unknown}

import scala.concurrent.duration.{Duration, NANOSECONDS}
import scala.concurrent.{ExecutionContext, Future}

trait Timer extends EventSource with DefaultEventRecorder {

  def timer[T](alertCode: AlertCode = Unknown)(function: => Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = {
    val start = System.nanoTime

      function.andThen {
        case _ =>
          val stop = System.nanoTime
          val elapsed = stop - start

          record(createTimerEvent(alertCode, Duration(elapsed, NANOSECONDS)))
      }
    }

  protected def createTimerEvent(alertCode: AlertCode, duration: Duration): Measurable = DefaultTimerEvent(source, alertCode, duration)
}
