import autobind from "autobind-decorator";
import { shallow, ShallowWrapper } from "enzyme";
import { Container, interfaces } from "inversify";
import React from "react";
import { Component, ComponentClass, ComponentState } from "react";

type TestBedInit<P, S> = {
    component: ComponentClass<P, S>,
    dependencies: TestBedDependency<any>[]
}

type TestBedDependency<T> = {
    identifier: interfaces.ServiceIdentifier<T>,
    value: T
}

@autobind
export class TestBed<
    T extends Component<P, S>,
    P extends {} = T["props"],
    S extends ComponentState = T["state"]
> {
    private componentType: ComponentClass<P, S>;
    private props: P;
    private container: Container;

    private constructor() {}

    public static create<T extends Component<P, S>, P = T["props"], S = T["state"]>({ component, dependencies }: TestBedInit<P, S>): TestBed<T, P, S> {
        let testBed = new TestBed<T, P, S>();
        testBed.componentType = component;
        testBed.container = TestBed.initContainer(dependencies);
        return testBed;
    }

    private static initContainer(dependencies: TestBedDependency<any>[]): Container {
        let container = new Container({
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
