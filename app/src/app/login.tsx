/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: Login screen with GitHub and Google sign-in buttons.
 * @since 11/16/2026
 */

import { Linking, Pressable, StyleSheet, Text, View } from 'react-native';

const BACKEND_URL = 'http://localhost:8080';

async function loginWith(provider: 'github' | 'google') {
    try {
        await Linking.openURL(
            `${BACKEND_URL}/oauth2/authorization/${provider}`
        );
    } catch (error) {
        console.error('Could not open the login link:', error);
    }
}

export default function LoginScreen() {
    return (
        <View style={styles.container}>
            <View style={styles.heading}>
                <Text style={styles.duck}>🦆</Text>
                <Text style={styles.title}>Rate My Duck</Text>
                <Text style={styles.description}>
                    A community-affirmed leaderboard of rubber ducks.
                </Text>
            </View>

            <View style={styles.buttons}>
                <Pressable
                    accessibilityRole="button"
                    onPress={() => loginWith('github')}
                    style={({ pressed }) => [
                        styles.button,
                        styles.githubButton,
                        pressed && styles.pressed,
                    ]}
                >
                    <Text style={styles.githubText}>Login with GitHub</Text>
                </Pressable>

                <Pressable
                    accessibilityRole="button"
                    onPress={() => loginWith('google')}
                    style={({ pressed }) => [
                        styles.button,
                        styles.googleButton,
                        pressed && styles.pressed,
                    ]}
                >
                    <Text style={styles.googleText}>Login with Google</Text>
                </Pressable>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#EAF6FF',
        alignItems: 'center',
        paddingHorizontal: 24,
        paddingTop: 48,
        paddingBottom: 32,
    },
    heading: {
        alignItems: 'center',
        width: '100%',
        maxWidth: 360,
    },
    duck: {
        fontSize: 56,
        marginBottom: 12,
    },
    title: {
        fontSize: 32,
        fontWeight: 'bold',
        color: '#17324D',
        textAlign: 'center',
    },
    description: {
        fontSize: 16,
        lineHeight: 24,
        color: '#465D73',
        textAlign: 'center',
        marginTop: 12,
        maxWidth: 320,
    },
    buttons: {
        width: '100%',
        maxWidth: 340,
        gap: 16,
        marginTop: 48,
    },
    button: {
        minHeight: 56,
        borderRadius: 16,
        paddingVertical: 16,
        paddingHorizontal: 24,
        alignItems: 'center',
        justifyContent: 'center',
    },
    githubButton: {
        backgroundColor: '#24292F',
    },
    googleButton: {
        backgroundColor: '#FFFFFF',
        borderWidth: 1,
        borderColor: '#C7D8E6',
    },
    githubText: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: '600',
        textAlign: 'center',
    },
    googleText: {
        color: '#17324D',
        fontSize: 16,
        fontWeight: '600',
        textAlign: 'center',
    },
    pressed: {
        opacity: 0.75,
    },
});