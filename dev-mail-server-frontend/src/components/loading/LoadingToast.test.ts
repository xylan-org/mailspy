import { Toast } from "react-bootstrap";
import { TestBed } from "test-utils/TestBed";
import { LoadingToast } from "./LoadingToast";

describe("LoadingToast", () => {
    
    let testBed: TestBed<LoadingToast>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: LoadingToast
        });
    });

    it("should have block display when 'show' prop is true", () => {
        // GIVEN
        testBed.setProps({
            show: true
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "block");
    });

    it("should be hidden when 'show' prop is false", () => {
        // GIVEN
        testBed.setProps({
            show: false
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "none");
    });

});