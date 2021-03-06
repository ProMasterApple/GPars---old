// GPars - Groovy Parallel Systems
//
// Copyright © 2008-11  The original author or authors
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

package groovyx.gpars.samples.benchmarks

import groovyx.gpars.dataflow.DataflowQueue
import static groovyx.gpars.dataflow.Dataflow.operator

final DataflowQueue a = new DataflowQueue()
final DataflowQueue b = new DataflowQueue()
final DataflowQueue c = new DataflowQueue()

def operator = operator([inputs: [a, b], outputs: [c]]) {x, y ->
    bindOutput(x + y)
}
final int iterations = 1000000

def t1 = System.currentTimeMillis()
iterations.times {
    a << it
    b << it
}

iterations.times {
    c.val
}

def t2 = System.currentTimeMillis()

//operator.stop()

println t2 - t1

System.exit(0)
