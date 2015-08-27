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

package uk.gov.hmrc.play.events.monitoring

import uk.gov.hmrc.play.audit.http.HeaderCarrier
import uk.gov.hmrc.play.events.DefaultEventRecorder
import uk.gov.hmrc.play.events.monitoring.Monitor.AlertCode
import uk.gov.hmrc.play.http.{HttpException, Upstream4xxResponse, Upstream5xxResponse}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.Failure

trait Monitor {
  def monitor[T](alertCode: AlertCode = "Unknown")(future: Future[T])(implicit hc: HeaderCarrier): Future[T] = future
}

object Monitor {

  type AlertCode = String
}

trait HttpMonitor extends Monitor with DefaultEventRecorder {

  def source: String

  override def monitor[T](alertCode: AlertCode = "Unknown")(future: Future[T])(implicit hc: HeaderCarrier): Future[T] = {
    super.monitor(alertCode) {
      future.andThen {
        case Failure(exception: Upstream5xxResponse) => record(DefaultHttpErrorEvent(source, exception, alertCode))
        case Failure(exception: Upstream4xxResponse) => record(DefaultHttpErrorEvent(source, exception, alertCode))
        case Failure(exception: HttpException) => record(DefaultHttpErrorEvent(source, exception, alertCode))
      }
    }
  }
}