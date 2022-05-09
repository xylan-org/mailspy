import { HttpService } from "./HttpService";

describe("HttpService", () => {
    let underTest: HttpService;
    let fetch: jest.Mock<Promise<Response>>;
    let getCsrfMeta: jest.Mock<HTMLMetaElement>;
    let environment: Record<string, string>;
    let window: Partial<Window>;

    beforeEach(() => {
        fetch = jest.fn();
        getCsrfMeta = jest.fn();
        environment = {};
        window = {};
        underTest = new HttpService(fetch, getCsrfMeta, environment, window);
    });

    describe("fetch", () => {
        const csrfToken = "fakeCsrfToken";

        beforeEach(() => {
            (environment.NODE_ENV = "development"), (environment.REACT_APP_BACKEND_ROOT = "http://example-test.com");
            getCsrfMeta.mockReturnValue({ content: csrfToken } as HTMLMetaElement);
        });

        it("should invoke fetch function with URL prefixed with base when environment is development", () => {
            // GIVEN
            fetch.mockResolvedValue(new Response("response"));

            // WHEN
            underTest.fetch("/test");

            // THEN
            expect(fetch).toHaveBeenCalledWith("http://example-test.com/test", { headers: {} });
        });

        it("should invoke fetch function with URL prefixed with window origin and path when environment is not development", () => {
            // GIVEN
            (environment.NODE_ENV = "production"),
                ((window.location as Partial<Location>) = {
                    origin: "http://example-prod.com",
                    pathname: "/path"
                });
            fetch.mockResolvedValue(new Response("response"));

            // WHEN
            underTest.fetch("/test");

            // THEN
            expect(fetch).toHaveBeenCalledWith("http://example-prod.com/path/test", { headers: {} });
        });

        it("should invoke fetch function with CSRF token when method is state-mutating", () => {
            // GIVEN
            fetch.mockResolvedValue(new Response("response"));

            // WHEN
            underTest.fetch("/test", { method: "POST" });

            // THEN
            expect(fetch).toHaveBeenCalledWith("http://example-test.com/test", {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": csrfToken
                }
            });
        });

        it("should throw error when response is not OK", async () => {
            // GIVEN
            fetch.mockResolvedValue(new Response("error", { status: 420 }));

            // WHEN
            const result = underTest.fetch("/test");

            // THEN
            await expect(result).rejects.toBeTruthy();
        });

        it("should return parsed JSON response when content-type is JSON", async () => {
            // GIVEN
            fetch.mockResolvedValue(
                new Response('{ "key": "value" }', {
                    status: 200,
                    headers: {
                        "content-type": "application/json"
                    }
                })
            );

            // WHEN
            const result = underTest.fetch("/test");

            // THEN
            await expect(result).resolves.toEqual({ key: "value" });
        });

        it("should return undefined when content-type is not JSON", async () => {
            // GIVEN
            fetch.mockResolvedValue(
                new Response("raw", {
                    status: 200,
                    headers: {
                        "content-type": "text/plain"
                    }
                })
            );

            // WHEN
            const result = underTest.fetch("/test");

            // THEN
            await expect(result).resolves.toBeUndefined();
        });
    });
});
