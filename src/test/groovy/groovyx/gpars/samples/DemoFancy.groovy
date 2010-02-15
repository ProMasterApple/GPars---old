// GPars (formerly GParallelizer)
//
// Copyright © 2008-9  The original author or authors
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

package groovyx.gpars.samples

import groovy.swing.SwingBuilder
import groovyx.gpars.Parallelizer
import groovyx.gpars.dataflow.DataFlow
import groovyx.gpars.dataflow.DataFlows
import java.awt.Color
import java.awt.GridLayout
import javax.swing.JFrame

def values = [1, 2, 3, 4, 5]
final DataFlows df = new DataFlows()

final SwingBuilder builder = new SwingBuilder()

builder.build {
    final JFrame frame = builder.frame(title: 'Demo', defaultCloseOperation: JFrame.EXIT_ON_CLOSE, visible: true) {
        panel(layout: new GridLayout(values.size(), 2)) {
            values.eachWithIndex {value, index ->
                button('Undefined', enabled: false, id: 'x' + index)
                button('Unknown', enabled: false, id: 'y' + index)

            }
        }

    }

    frame.pack()
}

DataFlow.task {
    values.eachWithIndex {value, index ->
        builder.edt {
            builder."y$index".text = 'Waiting'
            builder."y$index".background = Color.red
        }
        def result = df."$index"
        builder.edt {
            builder."y$index".text = 'Processing ' + result
            builder."y$index".background = Color.blue
        }
        sleep 1000
        builder.edt {
            builder."y$index".text = 'Done'
            builder."y$index".background = Color.green
        }
    }
}

values.eachWithIndex {value, index ->
    df."$index" {newValue ->
        builder.edt {
            builder."x$index".text = newValue
            builder."x$index".background = Color.green
        }
    }
}

random = new Random()

Parallelizer.doParallel(3) {
    values.eachWithIndexParallel {value, index ->
        builder.edt {
            builder."x$index".text = 'Calculating'
            builder."x$index".background = Color.blue
        }

        df."$index" = func(value)
    }
}

private def func(value) {
    sleep random.nextInt(15000)
    value
}

