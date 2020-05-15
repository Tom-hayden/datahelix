/*
 * Copyright 2019 Scott Logic Ltd
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

package com.scottlogic.datahelix.generator.core.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.datahelix.generator.core.config.detail.DataGenerationType;
import com.scottlogic.datahelix.generator.core.generation.GenerationConfigSource;
import com.scottlogic.datahelix.generator.core.generation.combinationstrategies.*;

public class CombinationStrategyProvider  implements Provider<CombinationStrategy> {
    private final GenerationConfigSource config;

    @Inject
    public CombinationStrategyProvider(GenerationConfigSource config){
        this.config = config;
    }

    @Override
    public CombinationStrategy get() {
        if (config.getGenerationType() == DataGenerationType.RANDOM){
            // The minimal combination strategy doesn't reuse values for fields.
            // This is required to get truly random data.
            return new ExhaustiveCombinationStrategy();
        }

        switch(config.getCombinationStrategyType()){
            case EXHAUSTIVE: return new ExhaustiveCombinationStrategy();
            case PINNING: return new PinningCombinationStrategy();
            case MINIMAL: return new MinimalCombinationStrategy();
            default:
                throw new UnsupportedOperationException(
                    "$Combination strategy {this.combinationStrategy} is unsupported.");
        }
    }
}
