import { Toast } from "react-bootstrap";
import { TestBed } from "test-utils/TestBed";
import { ErrorToast } from "./ErrorToast";

describe("ErrorToast", () => {

    let testBed: TestBed<ErrorToast>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: ErrorToast
        });
    });

    it("should have block display when 'show' prop is true", () => {
        // GIVEN
        testBed.setProps({
            show: true,
            message: ""
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "block");
    });

    it("should be hidden when 'show' prop is false", () => {
        // GIVEN
        testBed.setProps({
            show: false,
            message: ""
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "none");
    });

    it("should display message", () => {
        // GIVEN
        const message = "The apocalypse has struck."
        testBed.setProps({
            show: true,
            message
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(".error-toast-message").text()).toEqual(message);
    });

    it("should display retry button when 'retry' function is defined", () => {
        // GIVEN
        testBed.setProps({
            show: true,
            message: "",
            retry: () => {}
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.exists(".error-toast-retry")).toBeTruthy();
    });

    it("should not display retry button when 'retry' function not is defined", () => {
        // GIVEN
        testBed.setProps({
            show: true,
            message: ""
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.exists(".error-toast-retry")).toBeFalsy();
    });

    it("should call 'retry' function when it is defined and retry button is clicked", () => {
        // GIVEN
        const retryFunction = jest.fn();
        testBed.setProps({
            show: true,
            message: "",
            retry: retryFunction
        });
        const result = testBed.render();

        // WHEN
        result.find(".error-toast-retry").simulate("click");

        // THEN
        expect(retryFunction).toHaveBeenCalled();
    });
});