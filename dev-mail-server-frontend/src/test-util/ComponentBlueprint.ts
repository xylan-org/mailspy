import { shallow, ShallowWrapper } from "enzyme";
import { Container } from "inversify";
import React from "react";
import { ClassAttributes, ClassType, Component, ComponentClass, ComponentState } from "react";
import { DependencySetup } from "./DependencySetup";

export class ComponentBlueprint<P extends {}, S extends ComponentState, T extends Component<P, S>, C extends ComponentClass<P>> {
    private _component: ClassType<P, T, C>
    private _props: ClassAttributes<T> & P;
    private _container: Container;

    public constructor(component: ClassType<P, T, C>) {
        this._container = new Container({
            defaultScope: "Singleton"
        });
        this._component = component;
    }

    public static create<P extends {}, S extends ComponentState, T extends Component<P, S>, C extends ComponentClass<P>>(component: ClassType<P, T, C>): ComponentBlueprint<P, S, T, C> {
        return new ComponentBlueprint<P, S, T, C>(component);
    }

    public props(props: ClassAttributes<T> & P): ComponentBlueprint<P, S, T, C> {
        this._props = props;
        return this;
    }

    public dependencies(dependencies: DependencySetup<any>[]): ComponentBlueprint<P, S, T, C> {
        dependencies.forEach((dependency: DependencySetup<any>) => {
            this._container.bind<any>(dependency.identifier).toConstantValue(dependency.value);
        });
        return this;
    }

    public render(): ShallowWrapper<P, ComponentState, T> {
        const element = React.createElement(this._component, this._props);
        return shallow(element, {
            context: {
                container: this._container
            }
        });
    }
}
