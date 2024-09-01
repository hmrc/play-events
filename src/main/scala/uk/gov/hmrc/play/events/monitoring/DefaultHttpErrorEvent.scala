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

package uk.gov.hmrc.play.events.monitoring

import uk.gov.hmrc.play.events.AlertLevel._
import uk.gov.hmrc.play.events.{AlertCode, Alertable, Measurable}
import uk.gov.hmrc.http.{HttpException, UpstreamErrorResponse}

case class DefaultHttpErrorEvent(source: String,
                                 name: String,
                                 level: AlertLevel,
                                 alertCode: AlertCode,
                                 data: Map[String, String]) extends Alertable with Measurable

object DefaultHttpErrorEvent {

  def apply(source: String, response: UpstreamErrorResponse, alertCode: AlertCode): DefaultHttpErrorEvent = {
    response match {
      case UpstreamErrorResponse.Upstream4xxResponse(_) =>
        new DefaultHttpErrorEvent(
          source = source,
          name = "Http4xxError",
          level = MAJOR,
          alertCode = alertCode,
          data = Map(
            "Error Message" -> response.message,
            "Code" -> response.statusCode.toString,
            "Report As" -> response.reportAs.toString
          )
        )
      case _ =>
        new DefaultHttpErrorEvent(
          source = source,
          name = "Http5xxError",
          level = CRITICAL,
          alertCode = alertCode,
          data = Map(
            "Error Message" -> response.message,
            "Code" -> response.statusCode.toString,
            "Report As" -> response.reportAs.toString
          )
        )
    }
  }

  def apply(source: String, exception: HttpException, alertCode: AlertCode) = new DefaultHttpErrorEvent(
    source = source,
    name = if (exception.responseCode >= 500) "Http5xxError" else "Http4xxError",
    level = if (exception.responseCode >= 500) CRITICAL else MAJOR,
    alertCode = alertCode,
    data = Map(
      "Error Message" -> exception.message,
      "Code" -> exception.responseCode.toString
    )
  )
}
