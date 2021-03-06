// GPars - Groovy Parallel Systems
//
// Copyright © 2008--2011  The original author or authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package groovyx.gpars.pa;

import extra166y.Ops;
import groovy.lang.Closure;

/**
 * A PA predicate built around a closure
 *
 * @author Vaclav Pech
 */
public final class ClosurePredicate implements Ops.Predicate<Object> {
    private final Closure code;

    public ClosurePredicate(final Closure code) {
        this.code = code;
    }

    @Override
    public boolean op(final Object o) {
        return GParsPoolUtilHelper.convertToBoolean(code.call(o));
    }
}
