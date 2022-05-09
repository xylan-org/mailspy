/// <reference types="react-scripts" />

declare type Empty = Record<string, never>;

// @types/mailparser is flawed, SimpleParserOptions type is missing almost all properties
declare module "mailparser" {
    export interface ParsedMail {
        text: string;
        html: string;
    }
    export function simpleParser(source: Source, options?: Record<string, any>): Promise<ParsedMail>;
}
