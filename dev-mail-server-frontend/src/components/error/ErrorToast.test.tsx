import { shallow } from "enzyme";
import { Toast } from "react-bootstrap";
import { ErrorToast } from "./ErrorToast";

describe("ErrorToast", () => {
    it("should have block display when 'show' prop is true", () => {
        // GIVEN
        const underTest = shallow(
            <ErrorToast show={true} message="" />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(Toast).prop("style")).toHaveProperty("display", "block");
    });

    it("should be hidden when 'show' prop is false", () => {
        // GIVEN
        const underTest = shallow(
            <ErrorToast show={false} message="" />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(Toast).prop("style")).toHaveProperty("display", "none");
    });

    it("should display message", () => {
        // GIVEN
        const message = "The apocalypse has struck."
        const underTest = shallow(
            <ErrorToast show={true} message={message} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(".error-toast-message").text()).toEqual(message);
    });

    it("should display retry button when 'retry' function is defined", () => {
        // GIVEN
        const underTest = shallow(
            <ErrorToast show={true} message="" retry={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.exists(".error-toast-retry")).toBeTruthy();
    });

    it("should not display retry button when 'retry' function not is defined", () => {
        // GIVEN
        const underTest = shallow(
            <ErrorToast show={true} message="" />
        );

        // WHEN rendered

        // THEN
        expect(underTest.exists(".error-toast-retry")).toBeFalsy();
    });

    it("should call 'retry' function when it is defined and retry button is clicked", () => {
        // GIVEN
        const retryFunction = jest.fn();
        const underTest = shallow(
            <ErrorToast show={true} message="" retry={retryFunction} />
        );

        // WHEN
        underTest.find(".error-toast-retry").simulate("click");

        // THEN
        expect(retryFunction).toHaveBeenCalled();
    });
});