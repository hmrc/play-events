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

package uk.gov.hmrc.play.events.handlers

import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.events.{Measurable, Recordable}

object DefaultMetricsEventHandler extends MetricsEventHandler {
  override def handleMeasurable(measurable: Measurable) = Logger.info(s"metric:source:${measurable.source}:name:${measurable.name}:data:${measurable.data}")
}

trait MetricsEventHandler extends EventHandler {

  def handleMeasurable(measurable: Measurable)

  override def handle(event: Recordable)(implicit headerCarrier: HeaderCarrier) = {
    event match {
      case measurable: Measurable => handleMeasurable(measurable)
      case _ =>
    }
  }
}
