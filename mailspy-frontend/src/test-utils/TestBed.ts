/*
 * Copyright (c) 2022 xylan.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import autobind from "autobind-decorator";
import { shallow, ShallowWrapper } from "enzyme";
import { Container, interfaces } from "inversify";
import React from "react";
import { Component, ComponentClass, ComponentState } from "react";

type TestBedInit<P, S> = {
    component: ComponentClass<P, S>;
    dependencies?: TestBedDependency<any>[];
    props?: P;
};

type TestBedDependency<T> = {
    identifier: interfaces.ServiceIdentifier<T>;
    value: T;
};

@autobind
export class TestBed<T extends Component<P, S>, P extends unknown = T["props"], S extends ComponentState = T["state"]> {
    private componentType: ComponentClass<P, S>;
    private props: P;
    private container: Container;

    private constructor() {
        // empty
    }

    public static create<T extends Component<P, S>, P = T["props"], S = T["state"]>({ component, dependencies, props }: TestBedInit<P, S>): TestBed<T, P, S> {
        const testBed = new TestBed<T, P, S>();
        testBed.componentType = component;
        testBed.container = TestBed.initContainer(dependencies || []);
        testBed.props = props;
        return testBed;
    }

    private static initContainer(dependencies: TestBedDependency<any>[]): Container {
        const container = new Container({
            defaultScope: "Singleton"
        });
        dependencies.forEach((dependency: TestBedDependency<any>) => {
            container.bind<any>(dependency.identifier).toConstantValue(dependency.value);
        });
        return container;
    }

    public setProps(props: P): void {
        this.props = props;
    }

    public render(): ShallowWrapper<P, S, T> {
        const element = React.createElement(this.componentType, this.props);
        return shallow(element, {
            context: {
                container: this.container
            }
        });
    }
}
