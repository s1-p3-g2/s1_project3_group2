/**
 * Configures Expo component tests.
 * Looks for tests in __tests__, replaces CSS with a placeholder,
 * and resolves @/ imports to src.
 */
module.exports = {
    preset: 'jest-expo',
    clearMocks: true,

    testMatch: ['<rootDir>/__tests__/**/*.test.tsx'],

    moduleNameMapper: {
        '\\.css$': '<rootDir>/__mocks__/styleMock.js',
        '^@/(.*)$': '<rootDir>/src/$1',
    },
};