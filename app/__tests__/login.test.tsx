/**
 * Tests the login screen's GitHub and Google buttons.
 *
 * Each test checks that the named button exists and that pressing it
 * requests the correct backend OAuth URL.
 *
 * Linking.openURL is mocked so no browser opens and no backend,
 * database, or OAuth credentials are required.
 *
 * These tests do not verify successful authentication or the
 * backend's redirect after login.
 */

import { afterEach, describe, expect, jest, test } from '@jest/globals';
import { fireEvent, render } from '@testing-library/react-native';
import { Linking } from 'react-native';

import LoginScreen from '../src/app/login';

describe('Login screen', () => {
    // Restore the real function after each test to keep tests independent.
    afterEach(() => {
        jest.restoreAllMocks();
    });

    /**
     * Finds the GitHub button by its accessible role and label.
     * Presses it and checks that it opens the GitHub OAuth endpoint once.
     * Finding the button also verifies that it exists on the screen.
     */
    test('GitHub button exists and opens the GitHub OAuth URL', async () => {
        const openURL = jest
            .spyOn(Linking, 'openURL')
            .mockResolvedValue(undefined);

        const { getByRole } = await render(<LoginScreen />);

        const githubButton = getByRole('button', {
            name: 'Login with GitHub',
        });

        await fireEvent.press(githubButton);

        expect(openURL).toHaveBeenCalledTimes(1);
        expect(openURL).toHaveBeenCalledWith(
            'http://localhost:8080/oauth2/authorization/github'
        );
    });

    /**
     * Finds the Google button by its accessible role and label.
     * Presses it and checks that it opens the Google OAuth endpoint once.
     * Finding the button also verifies that it exists on the screen.
     */
    test('Google button exists and opens the Google OAuth URL', async () => {
        const openURL = jest
            .spyOn(Linking, 'openURL')
            .mockResolvedValue(undefined);

        const { getByRole } = await render(<LoginScreen />);

        const googleButton = getByRole('button', {
            name: 'Login with Google',
        });

        await fireEvent.press(googleButton);

        expect(openURL).toHaveBeenCalledTimes(1);
        expect(openURL).toHaveBeenCalledWith(
            'http://localhost:8080/oauth2/authorization/google'
        );
    });
});