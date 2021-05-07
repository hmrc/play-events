/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.{Recordable, Measurable, DefaultEventRecorder, AlertCode, Unknown}
import uk.gov.hmrc.http.{HttpException, Upstream4xxResponse, Upstream5xxResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

trait Monitor extends EventSource with DefaultEventRecorder {

  def monitor[T](alertCode: AlertCode)(future: Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = future
}

trait HttpErrorMonitor extends Monitor {

  override def monitor[T](alertCode: AlertCode = Unknown)(future: Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = {
    super.monitor(alertCode) {
      future.andThen {
        case Failure(exception: Upstream4xxResponse) => record(createHttpErrorEvent(source, exception, alertCode))
        case Failure(exception: Upstream5xxResponse) => record(createHttpErrorEvent(source, exception, alertCode))
        case Failure(exception: HttpException) => record(createHttpErrorEvent(source, exception, alertCode))
      }
    }
  }

  protected def createHttpErrorEvent(source: String, response: Upstream4xxResponse, alertCode: AlertCode): Recordable = DefaultHttpErrorEvent(source, response, alertCode)

  protected def createHttpErrorEvent(source: String, response: Upstream5xxResponse, alertCode: AlertCode): Recordable = DefaultHttpErrorEvent(source, response, alertCode)

  protected def createHttpErrorEvent(source: String, response: HttpException, alertCode: AlertCode): Recordable = DefaultHttpErrorEvent(source, response, alertCode)
}

trait HttpErrorCountMonitor extends Monitor {

  override def monitor[T](alertCode: AlertCode = Unknown)(future: Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = {
    super.monitor(alertCode) {
      future.andThen {
        case Failure(exception: Upstream4xxResponse) => record(createHttpErrorCountEvent(source, exception, alertCode))
        case Failure(exception: Upstream5xxResponse) => record(createHttpErrorCountEvent(source, exception, alertCode))
        case Failure(exception: HttpException) => record(createHttpErrorCountEvent(source, exception, alertCode))
      }
    }
  }

  protected def createHttpErrorCountEvent(source: String, response: Upstream4xxResponse, alertCode: AlertCode): Measurable = DefaultHttpErrorCountEvent(source, response, alertCode)

  protected def createHttpErrorCountEvent(source: String, response: Upstream5xxResponse, alertCode: AlertCode): Measurable = DefaultHttpErrorCountEvent(source, response, alertCode)

  protected def createHttpErrorCountEvent(source: String, response: HttpException, alertCode: AlertCode): Measurable = DefaultHttpErrorCountEvent(source, response, alertCode)
}
