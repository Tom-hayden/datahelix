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

package com.scottlogic.datahelix.generator.core.builders;

import com.scottlogic.datahelix.generator.core.profile.constraints.Constraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.grammatical.AndConstraint;

import java.util.List;

public class AndBuilder extends ConstraintChainBuilder<AndConstraint> {
    public AndBuilder() {
        super();
    }

    private AndBuilder(Constraint headConstraint, List<Constraint> tailConstraints) {
        super(headConstraint, tailConstraints);
    }

    public AndConstraint buildInner() {
        return new AndConstraint(tailConstraints);
    }

    @Override
    ConstraintChainBuilder<AndConstraint> create(Constraint headConstraint, List<Constraint> tailConstraints) {
        return new AndBuilder(headConstraint, tailConstraints);
    }
}
