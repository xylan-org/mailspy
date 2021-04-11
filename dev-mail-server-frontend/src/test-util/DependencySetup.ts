import { interfaces } from "inversify";

export interface DependencySetup<T> {
    identifier: interfaces.ServiceIdentifier<T>,
    value: T
}
