export interface ErrorToastProps {
    show: boolean;
    message: string;
    retry?: () => void;
}
