/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: Login screen with GitHub and Google sign-in buttons.
 * @since 11/16/2026
 */

import { Button, Linking, StyleSheet, View } from 'react-native';
//button is for clicking functions, linking for opening urls thru the backend
//stylesheet gives spacing and sizing alignment rules to view which is the layout container that holds other components.
export default function LoginScreen() {
    return (
        <View>
            <Button title="Login with GitHub" onPress={() => {}} />
            <Button title="Login with Google" onPress={() => {}} />
        </View>
    );
}