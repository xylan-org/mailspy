export interface ErrorProps {
    show: boolean;
    message: string;
    retry?: () => void;
}
