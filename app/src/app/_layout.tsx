import { DefaultTheme, ThemeProvider, Stack } from 'expo-router';
import { Colors } from '@/constants/theme';
// import * as SplashScreen from 'expo-splash-screen';

//these bits are commented out because the splash screen call tells expo to keep
// the splash screen open. and with that overlay disabled, the imports now unused
// and the color schemes caused a mismatch elsewhere.
// import { AnimatedSplashOverlay } from '@/components/animated-icon';
//
// SplashScreen.preventAutoHideAsync();

const colors = Colors.light;

const duckTheme = {
    ...DefaultTheme,
    colors: {
        ...DefaultTheme.colors,
        primary: colors.primary,
        background: colors.background,
        card: colors.backgroundElement,
        text: colors.text,
        border: colors.border,
        notification: colors.accent,
    },
};

export default function RootLayout() {
    return (
        <ThemeProvider value={duckTheme}>
            {/*<AnimatedSplashOverlay />*/}
            <Stack screenOptions={{ headerShown: false }} />
        </ThemeProvider>
    );
}