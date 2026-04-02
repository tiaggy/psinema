import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Text } from 'react-native';

import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import ProgramScreen from './src/screens/program/ProgramScreen';
import MovieDetailScreen from './src/screens/program/MovieDetailScreen';
import SeatSelectionScreen from './src/screens/booking/SeatSelectionScreen';
import SnackOrderScreen from './src/screens/booking/SnackOrderScreen';
import CheckoutScreen from './src/screens/booking/CheckoutScreen';
import MyTicketsScreen from './src/screens/mytickets/MyTicketsScreen';
import TicketDetailScreen from './src/screens/mytickets/TicketDetailScreen';
import AccountScreen from './src/screens/account/AccountScreen';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();
const qc = new QueryClient();

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: '#e94560',
        tabBarInactiveTintColor: '#888',
        tabBarIcon: ({ size }) => {
          const icons: Record<string, string> = { Program: '🎬', Tickets: '🎫', Account: '👤' };
          return <Text style={{ fontSize: size - 4 }}>{icons[route.name] ?? '•'}</Text>;
        },
      })}
    >
      <Tab.Screen name="Program" component={ProgramScreen} />
      <Tab.Screen name="Tickets" component={MyTicketsScreen} />
      <Tab.Screen name="Account" component={AccountScreen} />
    </Tab.Navigator>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={qc}>
      <NavigationContainer>
        <Stack.Navigator
          screenOptions={{
            headerStyle: { backgroundColor: '#1a1a2e' },
            headerTintColor: '#fff',
            headerTitleStyle: { fontWeight: '700' },
          }}
        >
          <Stack.Screen name="MainTabs" component={MainTabs} options={{ headerShown: false }} />
          <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'Sign In' }} />
          <Stack.Screen name="Register" component={RegisterScreen} options={{ title: 'Create Account' }} />
          <Stack.Screen name="MovieDetail" component={MovieDetailScreen} options={{ title: 'Movie Details' }} />
          <Stack.Screen name="SeatSelection" component={SeatSelectionScreen} options={{ title: 'Select Seats' }} />
          <Stack.Screen name="SnackOrder" component={SnackOrderScreen} options={{ title: 'Add Snacks' }} />
          <Stack.Screen name="Checkout" component={CheckoutScreen} options={{ title: 'Checkout' }} />
          <Stack.Screen name="TicketDetail" component={TicketDetailScreen} options={{ title: 'Ticket' }} />
        </Stack.Navigator>
      </NavigationContainer>
    </QueryClientProvider>
  );
}
