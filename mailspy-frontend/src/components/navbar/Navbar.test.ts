import { TestBed } from "test-utils/TestBed";
import { Navbar } from "./Navbar";
import BsNavbar from "react-bootstrap/Navbar";

describe("Navbar", () => {

    let testBed: TestBed<Navbar>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: Navbar
        });
    });

    it("should render branding", () => {
        // GIVEN
        // WHEN
        let result = testBed.render();

        // THEN
        expect(result.find(BsNavbar.Brand).text()).toEqual("MailSpy");
    });

});