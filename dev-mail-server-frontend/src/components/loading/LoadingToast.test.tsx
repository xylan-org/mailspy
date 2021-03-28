import { shallow } from "enzyme";
import { Toast } from "react-bootstrap";
import { LoadingToast } from "./LoadingToast";

describe("LoadingToast", () => {
    it("should have block display when 'show' prop is true", () => {
        // GIVEN
        const underTest = shallow(
            <LoadingToast show={true} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(Toast).prop("style")).toHaveProperty("display", "block");
    });

    it("should be hidden when 'show' prop is false", () => {
        // GIVEN
        const underTest = shallow(
            <LoadingToast show={false} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(Toast).prop("style")).toHaveProperty("display", "none");
    });
});