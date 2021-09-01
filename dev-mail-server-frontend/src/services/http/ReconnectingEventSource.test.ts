import { ReconnectingEventSource } from "./ReconnectingEventSource";
import FakeTimers from "@sinonjs/fake-timers";
import { when } from "jest-when";

describe("ReconnectingEventSource", () => {
    const EVENT_SOURCE_URL = "http://example.com/sse";

    let eventSourceMock: Partial<EventSource>;
    let clock: FakeTimers.Clock;
    let underTest: ReconnectingEventSource;
    
    beforeEach(() => {
        clock = FakeTimers.install();
        underTest = new ReconnectingEventSource(EVENT_SOURCE_URL, (url) => eventSourceMock as EventSource);
    });

    afterEach(() => {
        clock.uninstall();
    });

    describe("connect", () => {
        let closeMock: jest.Mock;
        let connectedMock: jest.Mock;
        let errorHandlerMock: jest.Mock;
        let addEventListenerMock: jest.Mock;

        beforeEach(() => {
            closeMock = jest.fn();
            connectedMock = jest.fn();
            errorHandlerMock = jest.fn();
            addEventListenerMock = jest.fn();

            underTest.onError(errorHandlerMock)
            underTest.onConnected(connectedMock);
            eventSourceMock = {
                close: closeMock,
                addEventListener: addEventListenerMock
            };
        });

        it("should close the event source and invoke error handlers if not connected within 1500 ms", async () => {
            // GIVEN in beforeEach()

            // WHEN
            underTest.connect();
            await clock.runAllAsync();

            // THEN
            expect(closeMock).toHaveBeenCalled();
            expect(errorHandlerMock).toHaveBeenCalled();
        });

        it("should invoke 'connected' event handlers and not invoke error handlers if connected within 1500 ms", async () => {
            // GIVEN
            const event = new Event("test");

            let eventSourceConnectedListener: EventListener;
            when(addEventListenerMock).calledWith(expect.stringMatching("connected"), expect.anything())
                .mockImplementation((type: string, listener: EventListener) => {
                    eventSourceConnectedListener = listener;
                });
            
            // WHEN
            underTest.connect();
            eventSourceConnectedListener(event);
            await clock.runAllAsync();

            // THEN
            expect(closeMock).not.toHaveBeenCalled();
            expect(connectedMock).toHaveBeenCalled();
        });

        it("should close previous connection and not invoke 'connected' event handler again when reconnected", async () => {
            // GIVEN
            const event = new Event("test");

            let eventSourceConnectedListener: EventListener;
            let eventSourceErrorListener: EventListener;
            when(addEventListenerMock).calledWith(expect.stringMatching("connected"), expect.anything())
                .mockImplementation((type: string, listener: EventListener) => {
                    eventSourceConnectedListener = listener;
                });
            when(addEventListenerMock).calledWith(expect.stringMatching("error"), expect.anything())
                .mockImplementation((type: string, listener: EventListener) => {
                    eventSourceErrorListener = listener;
                });
            
            // WHEN
            underTest.connect();
            // first connection
            eventSourceConnectedListener(event);
            await clock.runAllAsync();
            // error; disconnect
            eventSourceErrorListener(null);
            // second connection
            eventSourceConnectedListener(event);
            await clock.runAllAsync();

            // THEN
            expect(closeMock).toHaveBeenCalledTimes(1);
            expect(connectedMock).toHaveBeenCalledTimes(1);
        });

        
        it("should should invoke custom event handler when event source fires event with same name", async () => {
            // GIVEN
            const eventObject = new Event("test");
            const eventName = "testEvent";
            const customEventMock = jest.fn();

            let eventSourceCustomListener: EventListener;
            when(addEventListenerMock).calledWith(expect.stringMatching(eventName), expect.anything())
                .mockImplementation((type: string, listener: EventListener) => {
                    eventSourceCustomListener = listener;
                });

            underTest.onCustomEvent(eventName, customEventMock);

            // WHEN
            // event source connected
            underTest.connect();
            await clock.runAllAsync();
            // event source fired custom event
            eventSourceCustomListener(eventObject);

            // THEN
            expect(customEventMock).toHaveBeenCalledWith(eventObject);
        });
    });

});
